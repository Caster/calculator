# Calculator

This project is an example implementation of a simple CLI calculator. It is
meant to be part of a tutoring project where a learner develops a calculator in
increasing levels of complexity.

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
  - **V3**: add subtraction to your **V2** calculator. How much code do you need
    to change?
    - This might also be a good time to implement negative integers.
    - Does your code understand `1 + -2`? Should it?
    - Does your code understand `-4 -1`? Should it?
  - **V4**: add multiplication to your **V3** calculator. Be mindful of order of
    operations! How much code do you need to change?
    - Check that `3 + 2 * 5 = 13`, not `25`. 
  - **V5**: add division to your **V4** calculator. How much code do you need to
    change?
    - Initially you can have your calculator print `8 / 3 = 3`, but it might be
      nice to have it print something resembling `2.66667`. 
  - **V6**: add rational numbers to your **V5** calculator. How much code do you
    need to change?
    - Now your calculator should be able to compute that `0.5 * 4 = 2`.
    - Does your code understand `-3 * -4 = 12` too?
  - **V7**: add brackets to your **V6** calculator. How much code do you need to
    change?
    - Now your calculator should be able to compute that `(1 + 2) * 3 = 9` (and
      not `7`).

This repository contains an example implementation of a **V7** calculator.
