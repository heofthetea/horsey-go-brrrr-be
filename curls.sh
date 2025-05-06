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
  case $1 in
    "username")
      query_param="username=$2"
      ;;
    "id")
      query_param="id=$2"
      ;;
    *)
      echo "Invalid parameter. Use 'username', 'email', or 'id'." && return 1
      ;;
  esac
  curl --silent "$url/users?$query_param"
}


create_game() {
  user_id=$(get_user "username" "$1" | jq -r '.id')
  json="{\"host\": {\"id\": \"$user_id\"},
             \"width\": $2,
             \"height\": $3,
             \"state\": \"NOT_STARTED\"
           }"
  curl -vX POST "$url/games/create" \
    -H "Content-Type: application/json" \
    -d "$json"
}


get_games() {
  user_id=$(get_user "username" "$1" | jq -r '.id')
  curl --silent "$url/games?user_id=$user_id"
}