# GEICObot

Judges: We've been made aware that we forgot to include run/testing instructions for GEICObot.
We're updating the README to explain the process a little better:

## Notes for Judges
This is a standard Kotlin/JVM project using gradle.
The application entrypoint is located at `src/main/kotlin/com/adamratzman/geicobot/GeicoBot.kt`.

The src directory structure is consistent with the application design.

User chat -> Bot.kt chat endpoint -> CommandFactory -> Lex -> CommandFactory -> Command invocation

This means that user input is put through Lex. If one of our Lex command intents matches 
the input, we return lex.COMMAND_ID ARGS?, which is then mapped to a registered Command. The command's 
executeBase command is called and a callback provided for any return value. If no Lex intent 
is found, we return `lex.notfound`. This results in being matched to ScraperCommand, where 
we call the Cleverbot API for one of its conversational responses and return the response text.

As such, all `http` endpoints are located in `com.adamratzman.geicobot.http`.

Commands are located in `com.adamratzman.geicobot.commands` (design is also easily extensible)

Db integration is located in `com.adamratzman.geicobot.db`, and also in `GeicoBot.kt`.

Chat tools and utilities reside in `com.adamratzman.geicobot.chat`

See [Handlebars](https://jknack.github.io/handlebars.java/) and [Sparkjava](http://sparkjava.com/documentation) 
documentation if you are unfamiliar with Handlebars as a templating engine or Spark as a jetty-based web server.

See `build.gradle` for our plugins, jar packaging steps, and dependencies.

To test, please consult the "Testing non-locally" section below. You may use the account we provide 
in that section, which already contains a friends list, an existing chatlog, and favorites.

## Testing
### Non-locally
We have a hosted instance of GEICObot [here](http://144.217.240.243:8080/) that you can use. Please use the following test account 
that we created for the purposes of this hackathon (will be deleted on Tuesday):

u: adamratzman1 | p: geicohackathon

### Locally
It takes a lot of work to get an application of this scale running locally, and it's not free either, due to AWS/cleverbot.
That's why we provide a hosted instance and account for you to use.

However, we recognize that it may be important for you to test this application locally. 
You will need a valid cleverbot api key and aws account with permissions to call the Lex service.

Please follow these steps in order.

1. Make sure docker is installed on your system and that you are an administrator on your 
system.
2. Make sure your ~/.aws/config file looks like this:

```
[default]
region = us-east-1
output = text
```
2. `sh start-rethinkdb.sh` to start the rethinkdb server.
3. `java -jar ./build/libs/geico-hackathon-2-1.0-SNAPSHOT.jar CLEVERBOT_KEY AWS_PUBLIC_KEY AWS_SECRET_KEY`
4. Navigate to http://localhost:8080