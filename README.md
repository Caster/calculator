# Calculator

This project is an example implementation of a simple CLI calculator. It is
meant to be part of a tutoring project where a learner develops a calculator in
increasing levels of complexity.

**Starting off**

  - **V1**: a CLI app that asks for two numbers and prints their sum.
    - Keep in mind how you can test your code from the start. Write unit tests.
    - How do you handle invalid input?
    - Try to keep track of your code in Git and keep a clean history.
    - Play around with various methods of reading input and writing output.
      E.g., `System.in`/`System.out` but also `System.console`. The former can
      be interacted with in various ways too (`InputStream`, `Reader`, etc.).
  - **V2**: a CLI app that asks for one input, parsing e.g. `2+4` and printing
    the result. Start with only supporting non-negative integers and summation.
    - Keep those unit tests in mind! Does your program also understand `2 + 4`?
    - Start with a single addition, then work towards implementing multiple
      additions such as `1 + 2 + 3 + 4`.

**Ramping up difficulty**

  - **V3**: add subtraction to your **V2** calculator. How much code do you need
    to change?
    - This might also be a good time to implement negative integers.
    - Does your code understand `1 + -2`? Should it?
    - Does your code understand `-4 -1`? Should it?
  - **V4**: add multiplication to your **V3** calculator. Be mindful of order of
    operations! How much code do you need to change?
    - Check that `3 + 2 * 5 = 13`, not `25`.
    - Does your code understand `-3 * -4 = 12` too?
  - **V5**: add division to your **V4** calculator. How much code do you need to
    change?
    - Initially you can have your calculator print `8 / 3 = 3`, but it might be
      nice to have it print something resembling `2.66667`. 
  - **V6**: add rational numbers to your **V5** calculator. How much code do you
    need to change?
    - Now your calculator should be able to compute that `0.5 * 4 = 2`.

**Getting into a pickle**

  - **V7**: add brackets to your **V6** calculator. How much code do you need to
    change?
    - Now your calculator should be able to compute that `(1 + 2) * 3 = 9` (and
      not `7`).

**Advanced challenges**

  - **V8**: add implicit multiplication to your **V7** calculator. That is,
    parse `2(3)` as if it said `2*(3)` and solve to `6`.
    - This is tricky to get right, so no worries if this takes a while or if you
      do not want to finish this version, or if you do not get all corner cases
      to behave as you would like.
    - Think of cases like `(1)(2)(3)` and also `1(2)3(4(5))`.
    - You can take a peek at the `CalculatorTest` file in this repository to
      find more edge cases.
  - **V9**: add exponentiation to your **V8** (or **V7** if you skipped **V8**)
    calculator. You can use for example `**` or `^` as an operator; using two
    characters might be more challenging, depending on how you implemented your
    calculator so far.
    - Think of order of operations.
  - **V10**: add root extraction to your **V9** calculator. You can use full on
    `root` as an operator or the Unicode character `√` (or both, if you want,
    like this example implementation). It can work as `2√4 = 2` and `3√27 = 3`,
    so that it is still a binary operator.
    - Bonus points if you implement a unary square root operator (let's call
      that `sqrt`).

## Repository contents

This repository will contain an example implementation of a **V10** calculator
with unary operator support. Furthermore, it will contain all other versions
described above in earlier commits. To make this more clear, every commit that
makes the implementation reach the next version is tagged. This makes it easy to
browse an example implementation for every version, especially in GitHub.

This particular commit contains a **V3** implementation.

## Contributing

This repository is by no means perfect, so contributions are welcome. Feel free
to open a merge request for relatively small changes. Please open an issue to
discuss before you engage in large refactoring or adding large features.
