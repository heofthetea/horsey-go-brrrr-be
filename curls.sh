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
  json="{ \"host\": {\"username\": \"lirili larila\"},
             \"width\": $1,
             \"height\": $2,
             \"state\": \"NOT_STARTED\"
           }"
  curl --silent -vX POST "$url/games/create" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $3" \
    -d "$json"
}


get_games() {
  curl --silent "$url/games" -H "Authorization: Bearer $1"
}

get_game_by_id() {
  curl --silent "$url/games/$1"
}

# $1 = username - selects first game of the user
# $2 = username - selects the user to join
join_game(){
  json="I'm a teapot"
  curl --silent -X PUT "$url/games/$2/join" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $3" \
    -d "$json"
}

full_environment() {
  register_user test1 test 'test1@test.de'
  register_user test2 test 'test2@test.de'
  game_id=$(create_game test1 9 9 | jq -r '.id')
  join_game test2 "$game_id"
}

# join the player to their own game
frontend_environment() {
  register_user test1 test 'test1@test.de'
  game_id=$(create_game test1 9 9 | jq -r '.id')
  join_game test1 "$game_id"
}

get_game_history() {
  curl --silent "$url/games/$1/history"
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
  unset game_result
  while [[ 'HOST_WON' != "$game_result" ]]; do
    echo "Making turn for player: ${player_map[$player]}, $((player + 1))"
    make_turn "$game_id" "${player_map[$player]}" $((player + 1)) | jq -r '.state' > /tmp/game_result

    read -r game_result < /tmp/game_result
    player=$((player * -1))
  done
  echo "Game result: $game_result"
  echo "Game history:" && get_game_history "$game_id" | jq -r '.[].jen'

  if [ "$1" == "--delete-after" ]; then
    delete_game "$game_id"
  fi
}



# -------------------------------------------------------------------------------------------------
# oidc stuff
kchost=http://localhost:8081
realm=horsey-realm
client_id=horsey-api
secret=5P1jLzJpd3mmCBj466BVno257pO3xuk9
uname=test1
password='test'

obtain_access_token() {
  curl --silent -X POST "$kchost/realms/$realm/protocol/openid-connect/token" \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'grant_type=password' \
  --data-urlencode "client_id=$client_id" \
  --data-urlencode "client_secret=$secret" \
  --data-urlencode "username=$uname" \
  --data-urlencode "password=$password" | jq -r '.access_token' | tee /tmp/access_token
  read -r access_token < /tmp/access_token
}
