from data import item_df

item_df = item_df.sort_values(by='perfume_id', ascending=False)[:4]

popular_perfume_id = item_df['perfume_id'].tolist()