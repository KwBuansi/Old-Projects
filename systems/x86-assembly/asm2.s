# assemble/link with: gcc -m32 -o test asm1.s

.global main

.text

sum:
	# setup
	pushl %ebp
	movl	%esp, %ebp
	subl	$12, %esp

	movl	$5, -4(%ebp)      # -4(%ebp) <-- 5  (so x lives at %ebp-4)
	movl	$10, -8(%ebp)     # -8(%ebp) <-- 10 (so y lives at %ebp-8)
	movl	-4(%ebp), %edx    # %edx <-- x
	movl	-8(%ebp), %eax    # %eax <-- y
	addl	%edx, %eax        # %eax <-- x + y
	movl	%eax, -12(%ebp)   # -12(%ebp) <-- %eax (i.e, z <-- x + y)

	# return
	movl	-12(%ebp), %eax

	# cleanup
	leave
	ret

main:
	pushl	%ebp
	movl	%esp, %ebp
	#enter $0, $0

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
