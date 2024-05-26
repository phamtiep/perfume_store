import pandas as pd
import numpy as np

def count_perfumes_for_author(df, author_id):
    # Filter the DataFrame for the given author_id
    author_df = df[df['author_id'] == author_id]
    # Count the number of unique perfumes rated by the author
    num_perfumes = author_df['perfume_id'].nunique()
    return num_perfumes