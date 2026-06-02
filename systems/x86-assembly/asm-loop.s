	# assemble/link with: gcc -m32 -o test asm1.s

.global main

.text

sum:
  # setup
  pushl %ebp
  movl  %esp, %ebp
  subl  $8, %esp

  movl  $0, -4(%ebp)      # -4(%ebp) <-- 0  (so z lives at %ebp-4)
  movl  -4(%ebp), %eax
  movl  8(%ebp), %edx    # %edx <-- arg [FIRST ARGUMENT IS AT 8(%ebp)]
.begwhile:
  cmpl  $0, %edx
  jle   .donewhile
  addl  $100, %eax        # %eax <-- 100 + arg
  movl  %eax, -4(%ebp)   # -4(%ebp) <-- %eax (i.e, z <-- 100 + arg)
  subl  $1, %edx
  jmp   .begwhile
.donewhile:
 # return
  movl  -4(%ebp), %eax

  # cleanup
  leave
  ret

main:
  pushl %ebp
  movl  %esp, %ebp
  #enter $0, $0

  pushl $8
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