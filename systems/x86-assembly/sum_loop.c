
#include <stdio.h>

/* Example illustrating how data movement and arithmetic instructions
   are compiled into IA-32. */
int sum(int arg) {
  int z = 0;

  while(arg > 0) {
    z = 100 + arg;

    arg--;
  }

  return z;
}


/* Tester function. */
int main() {
  printf("Result: %d\n", sum(7));

  return 0;
}


