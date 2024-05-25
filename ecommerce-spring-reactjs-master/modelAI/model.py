import torch
import torch.optim.lr_scheduler
from torch.utils.data import Dataset, DataLoader
import tez
import pandas as pd
from sklearn import model_selection, preprocessing
import torch.nn as nn
from sklearn import metrics
import numpy as np
from tqdm import tqdm
from concat import merged_df

# Dataset Definition
class PerfumeDataset(Dataset):
    def __init__(self, users, perfumes, ratings):
        self.users = users
        self.perfumes = perfumes
        self.ratings = ratings
    
    def __len__(self):
        return len(self.users)
    
    def __getitem__(self, item):
        user = self.users[item]
        perfume = self.perfumes[item]
        rating = self.ratings[item]

        return {'user': torch.tensor(user, dtype=torch.long),
                'perfume': torch.tensor(perfume, dtype=torch.long),
                'rating': torch.tensor(rating, dtype=torch.float)}

# Model Definition
class RecSysModel(tez.Model):
    def __init__(self, num_users, num_perfumes):
        super().__init__()
        self.user_embed = nn.Embedding(num_users, 32)
        self.perfume_embed = nn.Embedding(num_perfumes, 32)
        self.out = nn.Linear(64, 1)
        self.step_scheduler_after = 'epoch'

    def fetch_optimizer(self):
        opt = torch.optim.Adam(self.parameters(), lr=1e-3)
        return opt
    
    def fetch_scheduler(self):
        sch = torch.optim.lr_scheduler.StepLR(self.optimizer, step_size=1, gamma=0.7)
        return sch
    
    def monitor_metrics(self, output, rating):
        output = output.detach().cpu().numpy()
        rating = rating.detach().cpu().numpy()
        return {
            'rmse': np.sqrt(metrics.mean_squared_error(rating, output))
        }

    def forward(self, users, perfumes, ratings=None):
        user_embeds = self.user_embed(users)
        perfume_embeds = self.perfume_embed(perfumes)
        output = torch.cat([user_embeds, perfume_embeds], dim=1)
        output = self.out(output)
        
        loss = nn.MSELoss()(output, ratings.view(-1,1))
        metrics = self.monitor_metrics(output, ratings.view(-1,1))
        return output, loss, metrics

    def get_embeddings(self):
        return self.user_embed.weight.data.cpu().numpy(), self.perfume_embed.weight.data.cpu().numpy()

# Training Function
def train():
    df = merged_df.copy()
    lbl_user = preprocessing.LabelEncoder()
    lbl_perfume = preprocessing.LabelEncoder()

    df['author_id'] = lbl_user.fit_transform(df['author_id'])
    df['perfume_id'] = lbl_perfume.fit_transform(df['perfume_id'])

    df_train, df_valid = model_selection.train_test_split(
        df, test_size=0.1, random_state=42, stratify=df['rating'].values
    )
    
    train_dataset = PerfumeDataset(
        users=df_train['author_id'].values, 
        perfumes=df_train['perfume_id'].values, 
        ratings=df_train['rating'].values
    )
    
    valid_dataset = PerfumeDataset(
        users=df_valid['author_id'].values, 
        perfumes=df_valid['perfume_id'].values, 
        ratings=df_valid['rating'].values
    )
    
    model = RecSysModel(num_users=len(lbl_user.classes_), num_perfumes=len(lbl_perfume.classes_))
    model.fit(train_dataset, valid_dataset, train_bs=1024, valid_bs=1024, fp16=True)
    torch.save(model.state_dict(), 'model.pth')  # Save the trained model

# Prediction Function
def predict(user_id, model, lbl_user, lbl_perfume):
    user_idx = lbl_user.transform([user_id])[0]
    user_tensor = torch.tensor([user_idx], dtype=torch.long)
    
    perfume_indices = range(len(lbl_perfume.classes_))
    perfume_tensor = torch.tensor(perfume_indices, dtype=torch.long)
    
    model.eval()
    
    with torch.no_grad():
        user_embeds = model.user_embed(user_tensor).repeat(len(perfume_tensor), 1)
        perfume_embeds = model.perfume_embed(perfume_tensor)
        output = model.out(torch.cat([user_embeds, perfume_embeds], dim=1))
    
    scores = output.squeeze().numpy()
    perfume_ids = lbl_perfume.inverse_transform(perfume_indices)
    
    recommendations = sorted(zip(perfume_ids, scores), key=lambda x: x[1], reverse=True)[:4]
    return [perfume_id for perfume_id, score in recommendations]

# Example Usage
if __name__ == '__main__':
    # Assuming merged_df is already loaded

    # Train the model
    train()

    # Load the trained model
    df = merged_df.copy()
    lbl_user = preprocessing.LabelEncoder()
    lbl_perfume = preprocessing.LabelEncoder()

    df['author_id'] = lbl_user.fit_transform(df['author_id'])
    df['perfume_id'] = lbl_perfume.fit_transform(df['perfume_id'])

    model = RecSysModel(num_users=len(lbl_user.classes_), num_perfumes=len(lbl_perfume.classes_))
    model.load_state_dict(torch.load('model.pth'))  # Adjust the path accordingly

    # Get recommendations for a specific user
    user_id = 'user1'
    recommendations = predict(user_id, model, lbl_user, lbl_perfume)
    
    
