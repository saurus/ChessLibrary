ChessLibrary
============

Simple Chess support Library in Java.

This is released under GPL v3.

I needed a PGN parser in Java, to support ChessWidget (https://github.com/saurus/ChessWidget) but found none that 
fitted my needs or was easy to adapt to.

As you may expect, this library contains abstraction to boards, pieces and moves. But as parsing PGN 
is a complex task, because a complete knowledge of the chess rules must be implemented to parse the moves, 
this library contains also a move generator ad a game rappresentation.

Keep in mind that those implementations exists only to support PGN parsing, so other uses, while teoretically possible 
(in fact, I have tried to write generic code), are not yet proven. 
