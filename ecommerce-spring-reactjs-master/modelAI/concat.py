from data import perfume_reviews_df, review_df
import pandas as pd

merged_df = pd.merge(perfume_reviews_df, review_df, left_on='review_id', right_on='id')

unique_authors = merged_df['author'].unique()

# gan id cho author
author2id = {author: idx for idx, author in enumerate(unique_authors)}

merged_df['author_id'] = merged_df['author'].map(author2id)

merged_df = merged_df.drop(['id', 'author', 'message', 'date', 'review_id'], axis=1)

