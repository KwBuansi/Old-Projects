
#include <stdio.h>

/* Example illustrating how data movement and arithmetic instructions
   are compiled into IA-32. */
int sum(int arg) {
  int x = 5;
  int z;

  if(arg == 42) {
    z = x;
  }
  else {
    z = 100 + arg;
  }

  return z;
}


/* Tester function. */
int main() {
  printf("Result: %d\n", sum(27));

  return 0;
}


