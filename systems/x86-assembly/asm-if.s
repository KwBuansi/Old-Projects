	# assemble/link with: gcc -m32 -o test asm1.s

.global main

.text

sum:
  # setup
  pushl %ebp
  movl  %esp, %ebp
  subl  $8, %esp

  movl  $5, -4(%ebp)      # -4(%ebp) <-- 5  (so x lives at %ebp-4)
  movl  -4(%ebp), %edx    # %edx <-- x
  movl  8(%ebp), %eax    # %eax <-- arg [FIRST ARGUMENT IS AT 8(%ebp)]
  cmpl  $42, %eax
  jne   .elsepart
  movl  %edx, %eax
  movl  %eax, -8(%ebp)   # -8(%ebp) <-- %eax (i.e, z <-- x)
  jmp   .done
.elsepart:
  addl  $100, %eax        # %eax <-- 100 + arg
  movl  %eax, -8(%ebp)   # -8(%ebp) <-- %eax (i.e, z <-- 100 + arg)
.done:
  # return
  movl  -8(%ebp), %eax

  # cleanup
  leave
  ret

main:
  pushl %ebp
  movl  %esp, %ebp
  #enter $0, $0

  pushl $42
  call sum

  pushl %eax
  pushl $msg
  call printf

  movl $0, %eax

  #mov esp, ebp
  #pop ebp
  leave

  ret

.data

msg:
  .string "Result: %d\n"