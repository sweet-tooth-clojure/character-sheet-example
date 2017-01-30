cd ..
export DB_URI="datomic:free://localhost:4334/character-sheet";
export HTTP_SERVER_PORT=4000
java -jar target/build/app.jar server
