host=localhost
port=8080
url=http://$host:$port/api

register_user(){
  curl -X POST "$url/users/register" \
    -H "Content-Type: application/json" \
    -d "{
      \"username\": \"$1\",
      \"password\": \"$2\",
      \"email\": \"$3\"
    }"
}

get_user(){
  query_param=""
  curl --silent "$url/users/$1"
}


create_game() {
  json="{\"host\": {\"username\": \"$1\"},
             \"width\": $2,
             \"height\": $3,
             \"state\": \"NOT_STARTED\"
           }"
  curl -vX POST "$url/games/create" \
    -H "Content-Type: application/json" \
    -d "$json"
}


get_games() {
  curl --silent "$url/games?user_id=$1"
}

join_game(){
  game=$(get_games $1 | jq -r '.[0].id')
  json="{\"username\": \"$1\"}"
  curl -vX PUT "$url/games/join/$game" \
    -H "Content-Type: application/json" \
    -d "$json"
}

full_environment() {
  register_user test1 test 'test1@test.de'
  create_game test1 5 5
  join_game test1

}

make_turn() {
  game=$(get_games $1 | jq -r '.[0].id')
  json="{\"user\": {\"username\": \"$1\"},
             \"column\": $2,
           }"
  curl -vX PUT "$url/games/move/$game" \
    -H "Content-Type: application/json" \
    -d "$json"
}