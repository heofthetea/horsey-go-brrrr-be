## JEN

FEN (Forsyth-Edwards-Notation)refers to a way to store chess positions as a string. As this game is 4-in-a row, a custom protocol needs to be defined - JEN (Josia-Emil-Notation). This is inspired by the chess FEN. In the following this will be gradually built-up and motivated by generating a Regular Expression to match a valid JEN string.

## String format

### 1. Board dimensions

A game board can be of arbitrary dimension (there aren't any restrictions in the database yet) - arbitrary in the sense of Bytes, i.e. $\leq 128$ (negative values are technically still supported currently) As such, the width and height of the board need to be specified, with three characters:

```regex
[0-9]{3}[0-9]{3}
```

the first number is the width, the second number the height.

#### Example

```regex
008012
```

for an 8 by 12 board

### 2. Player to move

There are two players: `x` and `o`.<br>
`x` shall refer to the host, `o` to the guest. <br>
The host always has the first move.<br><br>
The player to move shall be specified in the 7th digit:

```regex
[0-9]{3}[0-9]{3}[xo]
```

#### Example

```regex
008012x
```

### Board State

Now, the state of the board will be drawn as one continuous string.<br>
This string will be $width \cdot height$ characters long. These values can be inferred from the start of the JEN.

> This is where the RegEx would have to be generated based on the first 6 digits. For clarity, the variables w and h are used to refer to width and height respectively.

```regex
[0-9]{3}[0-9]{3}[xo](?[\-xo]{w}){h}
```

The String shall be built row-first left-to-right, starting with the lowest row. In other words, first the lowest row is fully drawn, with the first character being the bottom-left-most cell. Then, the second row, and so on until the n-th row.

## Summarizing Example

Assume the String

```
004003o-xox--xo----
```

is given. This should represent the following board:

```
| | | | |
| | |x|o|
| |x|o|x|
```
