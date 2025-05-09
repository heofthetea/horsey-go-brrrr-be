host=localhost
port=8080
url=http://$host:$port/api

register_user(){
  curl --silent -X POST "$url/users/register" \
    -H "Content-Type: application/json" \
    -d "{
      \"username\": \"$1\",
      \"password\": \"$2\",
      \"email\": \"$3\"
    }"
  echo ""
}

get_user(){
  curl --silent "$url/users/$1"
}


# $1 = username - selects first game of the user
# $2 = width - selects the width of the game
# $3 = height - selects the height of the game
create_game() {
  json="{\"host\": {\"username\": \"$1\"},
             \"width\": $2,
             \"height\": $3,
             \"state\": \"NOT_STARTED\"
           }"
  curl --silent -X POST "$url/games/create" \
    -H "Content-Type: application/json" \
    -d "$json"
}


get_games() {
  curl --silent "$url/games?user_id=$1"
}

get_game_by_id() {
  curl --silent "$url/games/$1"
}

# $1 = username - selects first game of the user
# $2 = username - selects the user to join
join_game(){
  json="{\"username\": \"$1\"}"
  curl --silent -X PUT "$url/games/join/$2" \
    -H "Content-Type: application/json" \
    -d "$json"
}

full_environment() {
  register_user test1 test 'test1@test.de'
  register_user test2 test 'test2@test.de'
  game_id=$(create_game test1 5 5 | jq -r '.id')
  join_game test2 "$game_id"
}

get_latest_position() {
  curl --silent "$url/games/$1/latest-position"
}

# $1 = game_id
# $2 = username - selects the user to make the turn
# $3 = column - selects the column to make the turn
make_turn() {
  json="{\"user\": {\"username\": \"$2\"},
             \"column\": $3
           }"
  curl --silent -X PUT "$url/games/$1/make-turn" \
    -H "Content-Type: application/json" \
    -d "$json"
}

delete_game() {
  curl --silent -X DELETE "$url/games/$1"
}

test_game_ops() {

  full_environment | tail -n 1 | jq -r .id > /tmp/game_id && read -r game_id < /tmp/game_id

  declare -A player_map=([-1]='test1' [1]='test2')
  player=-1
  while [[ 'HOST_WON' != "$game_result" ]]; do
    echo "Making turn for player: ${player_map[$player]}, $((player + 1))"
    make_turn "$game_id" "${player_map[$player]}" $((player + 1)) | jq -r '.game.state' > /tmp/game_result
    sleep 1
    read -r game_result < /tmp/game_result
    player=$((player * -1))
  done
  echo "Game result: $game_result"
  get_latest_position "$game_id"

  delete_game "$game_id"
}
