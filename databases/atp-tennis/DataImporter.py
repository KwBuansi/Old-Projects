import pandas as pd
from sqlalchemy import create_engine
from sqlalchemy.types import Integer, String, CHAR, Date
from datetime import timedelta

username = 'root'
password = '{Insert-password-here}'
host = 'localhost'
port = 3306
database = 'tennis'

connection_string = f'mysql+pymysql://{username}:{password}@{host}:{port}/{database}'

engine = create_engine(connection_string)

def findBirthDate(age, reference_date):
    if pd.isna(age) or pd.isna(reference_date):
        return None
    
    days = age * 365.25
    birthdate = reference_date - timedelta(days=days)
    return birthdate.date()

def insertPlayers(df):
    #combines winners and losers
    winners = df[['winner_id', 'winner_name', 'winner_hand', 'winner_ht', 'winner_ioc', 'winner_age', 'tourney_date']].copy()
    winners.columns = ['player_id', 'name', 'dominant_hand', 'height', 'IOC', 'age', 'tourney_date']

    losers = df[['loser_id', 'loser_name', 'loser_hand', 'loser_ht', 'loser_ioc', 'loser_age', 'tourney_date']].copy()
    losers.columns = ['player_id', 'name', 'dominant_hand', 'height', 'IOC', 'age', 'tourney_date']
    
    players = pd.concat([winners, losers], ignore_index=True).drop_duplicates(subset='player_id')

    #calculates DOB given age
    players['tourney_date'] = pd.to_datetime(players['tourney_date'])
    players['date_of_birth'] = players.apply(lambda row: findBirthDate(row['age'], row['tourney_date']), axis=1)
    players = players.drop(columns=['age', 'tourney_date'])

    #makes null/invalid hand types 'U'
    players['dominant_hand'] = players['dominant_hand'].fillna('U')  # fill missing
    players['dominant_hand'] = players['dominant_hand'].apply(lambda x: x if x in ['L', 'R', 'U'] else 'U')

    # queries existing player_ids from database
    existing_ids = pd.read_sql("SELECT player_id FROM players", con=engine)
    existing_ids_set = set(existing_ids['player_id'].tolist())

    # filters out those players
    players_to_insert = players[~players['player_id'].isin(existing_ids_set)]

    if not players_to_insert.empty:
        players_to_insert.to_sql('players', con=engine, if_exists='append', index=False,
                       dtype={
                           'player_id': Integer(),
                           'name': String(40),
                           'dominant_hand': CHAR(1),
                           'height': Integer(),
                           'IOC': CHAR(3),
                           'date_of_birth': Date()
                       })

def insertTournaments(df):
    tournaments = df[['tourney_id', 'tourney_name', 'surface', 'draw_size', 'tourney_level', 'tourney_date', 'best_of']].copy()
    tournaments = tournaments.drop_duplicates(subset='tourney_id')
    tournaments.columns = ['tourney_id', 'tourney_name', 'surface', 'draw_size', 'tourney_level', 'tourney_date', 'best_of']
    
    tournaments.to_sql('tournaments', con=engine, if_exists='append', index=False,
                   dtype={
                        'tourney_id': String(50),
                        'tourney_name': String(50),
                        'surface': String(6),
                        'draw_size': Integer(),
                        'tourney_level': String(1),
                        'tourney_date': Date(),
                        'best_of': Integer()
                   })
    
def insertMatches(df):
    matches = df[['match_num', 'score', 'round', 'minutes', 'winner_id', 'loser_id', 'tourney_id']].copy()
    matches = matches.drop_duplicates(subset=['tourney_id', 'match_num'])
    matches.columns = ['match_id', 'score', 'round_of_tourn', 'minutes', 'winner_id', 'loser_id', 'tourney_id']

    matches = matches[['tourney_id', 'match_id', 'score', 'round_of_tourn', 'minutes', 'winner_id', 'loser_id']]

    matches.to_sql('matches', con=engine, if_exists='append', index=False,
                   dtype={
                        'match_id': Integer(),
                        'tourney_id': String(50),
                        'score': String(50),
                        'round_of_tourn': String(4),
                        'minutes': Integer(),
                        'winner_id': Integer(),
                        'loser_id': Integer()
                   })


def insertPlayerInMatch(df):
    #again, combines winners and losers
    winner_in_match = df[['winner_id', 'match_num', 'tourney_id', 'w_ace', 'w_df', 'w_svpt', 'w_1stIn', 'w_1stWon', 'w_2ndWon', 'w_SvGms', 'w_bpSaved', 'w_bpFaced', 'winner_rank']].copy()
    winner_in_match.columns = ['player_id', 'match_id', 'tourney_id', 'ace', 'df', 'svpt', 'first_in', 'first_won', 'second_won', 'sv_Gms', 'bp_Saved', 'bp_Faced', 'player_rank']

    loser_in_match = df[['loser_id', 'match_num', 'tourney_id', 'l_ace', 'l_df', 'l_svpt', 'l_1stIn', 'l_1stWon', 'l_2ndWon', 'l_SvGms', 'l_bpSaved', 'l_bpFaced', 'loser_rank']].copy()
    loser_in_match.columns = ['player_id', 'match_id', 'tourney_id', 'ace', 'df', 'svpt', 'first_in', 'first_won', 'second_won', 'sv_Gms', 'bp_Saved', 'bp_Faced', 'player_rank']

    player_in_match = pd.concat([winner_in_match, loser_in_match], ignore_index=True)
    player_in_match = player_in_match.drop_duplicates(subset=['player_id', 'match_id', 'tourney_id'])

    player_in_match.to_sql('player_in_match', con=engine, if_exists='append', index=False,
                   dtype={
                        'player_id': Integer(),
                        'match_id': Integer(),
                        'tourney_id': String(50),
                        'ace': Integer(),
                        'df': Integer(),
                        'svpt': Integer(),
                        'first_in': Integer(),
                        'first_won': Integer(),
                        'second_won': Integer(),
                        'sv_Gms': Integer(),
                        'bp_Saved': Integer(),
                        'bp_Faced': Integer(),
                        'player_rank': Integer()
                   })

    
def populateDatabase():
    print("Populating tables...")
    for year in range (1968, 2025):
        df = pd.read_csv(f"C:/inesrt/path/to/csvs/here/atp_matches_{year}.csv")
        insertPlayers(df)
        insertTournaments(df)
        insertMatches(df)
        insertPlayerInMatch(df)
    print("Complete")

populateDatabase()

