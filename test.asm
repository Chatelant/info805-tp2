DATA SEGMENT
	v DD
	t DD
	p DD
DATA ENDS
CODE SEGMENT
	in eax
	mov v, eax
	in eax
	mov t, eax
	mov eax, 100
	push eax
	mov eax, v
	pop ebx
	mul eax, ebx
	push eax
	mov eax, t
	pop ebx
	div ebx, eax
	mov eax, ebx
	mov p, eax
	mov eax, p
	out eax
CODE ENDS
