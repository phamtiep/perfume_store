from concat import merged_df
from sklearn.decomposition import TruncatedSVD
import pandas as pd
import numpy as np

df = merged_df.pivot_table(index='author_id', columns='perfume_id', values='rating', fill_value=0)

num_latent_factors = 2
svd = TruncatedSVD(n_components=num_latent_factors)

svd.fit(df)


def recommend_items(user_id, svd_model, df, num_recommendations=8):

    user_row = df.loc[user_id]

    user_latent = svd_model.transform([user_row])

    all_items_latent = svd_model.transform(df)

    predicted_ratings = np.dot(user_latent, all_items_latent.T)

    top_indices = np.argsort(-predicted_ratings)[0][:num_recommendations]
    
    return top_indices

