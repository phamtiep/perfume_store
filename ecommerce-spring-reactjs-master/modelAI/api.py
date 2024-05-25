from flask import Flask, request, jsonify
from flask_restful import Resource, Api
import torch
from sklearn import preprocessing
import pandas as pd
from model import RecSysModel, PerfumeDataset, predict
from concat import author2id, merged_df
import numRate
from popularity import popular_perfume_id

# Initialize Flask app
app = Flask(__name__)
api = Api(app)

def load_model():
    model = RecSysModel(num_users=0, num_perfumes=0)  # Initialize model
    model.load_state_dict(torch.load('model.pth'))
    return model

# Initialize label encoders
def initialize_label_encoders(df):
    lbl_user = preprocessing.LabelEncoder()
    lbl_perfume = preprocessing.LabelEncoder()

    lbl_user.fit(df['author_id'])
    lbl_perfume.fit(df['perfume_id'])

    return lbl_user, lbl_perfume

# Route to recommend perfumes
class Recommend(Resource):
    def get(self, author):
        user_id = author2id.get(author)
        nRate = numRate.count_perfumes_for_author(merged_df, user_id)
        if (nRate < 3):
            return popular_perfume_id
        else:
            # Load model and label encoders
            model = load_model()
            lbl_user, lbl_perfume = initialize_label_encoders(merged_df)
    
            # Generate recommendations
            recommendations = predict(user_id, model, lbl_user, lbl_perfume)
    
            # Return recommendations
            return jsonify({'recommendations': recommendations})
    
api.add_resource(Recommend, '/recommend/<string:author>')

if __name__ == '__main__':
    app.run(debug=True)
