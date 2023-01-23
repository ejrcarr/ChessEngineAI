# ChessEngineAI

## Context
I created a Chess Engine AI that uses over 2,680,000+ past chess games from previous professional games to use as book moves. The engine uses a <a href='https://en.wikipedia.org/wiki/Minimax'>minimax algorithm</a> to determine the best next move for the computer. Specifically, the engine calculates best next move based on piece value, mobility, development, king safety, and pawn structure. The application is coded in Java, using Swing as the graphical user interface, and MySQL as the local database.

Making this game, I familiarized myself with Object-Oriented Programming, SOLID priciples, and the Swing library. 

## Why?
I enjoy playing chess in my free time which led to me to create a desktop app with <em>Java/Swing/MySQL</em>. Since most chess platforms have paywalls, I decided to create my own application to analyze my past games. The design was modeled after chess.com's website and the GUI works similar as well.  

I also wanted more experience with OOP and SOLID principles. 
<br />
<br />

<p align='center'>
  <img src='https://user-images.githubusercontent.com/110308975/214087508-d97e13e5-2ad0-4836-873e-06f00ea0ed29.gif' width="300" height="250"> <br />
</p>

## Features
### Game Aspects
The GUI follows chess procedure, highlights legal moves for a clicked piece, indicates taken pieces, and displays chess notation of game. Promoting to queen is allowed. En passant is also allowed. On click, the piece is highlighted to move to the next square the player clicks. 
### Options Panel
- #### File
  - File tab allows for the player to quit the game or load a PGN file to create a new game. 
- #### Preferences
  - The preferences tab allows the player to choose if they want to highlight legal moves on click, if they want to flip the board, or if they want to use book moves. Flipping board does not change which side the player plays on. Unselecting use book moves would not allow the computer to use the local database of previous games to determine theory. 
- #### Options
  - In the options tab, you can create a new game (hotkey N), undo the last move (hotkey Z), or setup game. With the setup option, you can determine how far moves in advance the algorithm calculates and if white or black is a player or computer. 
