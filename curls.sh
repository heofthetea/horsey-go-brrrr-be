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
}

get_user(){
  curl --silent "$url/users/$1"
}


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
  curl --silent -vX PUT "$url/games/$1/make-turn" \
    -H "Content-Type: application/json" \
    -d "$json"
}

delete_game() {
  curl --silent -X DELETE "$url/games/$1"
}
