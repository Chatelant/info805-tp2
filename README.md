# Compte Rendu : TP Compilation - Génération d'arbres abstraits
__Binôme__ : Antoine Chatel et Elodie Deflon

L'objectif du TP est d'utiliser les outils JFlex et CUP pour générer des arbres abstraits correspondant à un sous ensemble du langage **λ-ada**.

# Rapport
Dans __AnalyseurSyntaxique__ et __AnalyseurLexical__, nous identifions les différents morceaux des programmes et à partir de ceux-ci nous créons des __arbres imbriqués__ qui nous permettront de représenter l'arborescence du programme.
La racine de chaque arbre contient un type __"Operator"__ qui nous permets d'identifier les opérations effectuées sur les sous ensembles.

La méthode __"generate()"__ dans __"AssemblerGenerator.java"__ nous permets de générer le code assembleur en parcourant l'arbre résultat de manière __récursive__.

Le code assembleur est généré en 2 parcours récursifs : nous générons la partie __"DATA SEGMENT"__ en premier puis la partie __"CODE SEGMENT"__.
Enfin, nous écrivons le résultat dans le fichier __"test.asm"__ généré à la racine du projet.

Pour tester les résultats des exemples ci-dessous, utilisez dans votre console : `java -jar vm-0.9.jar test.asm`


# Exemples de génération de code

## Exemple 1

Programme :
```
let prixTtc =  prixHt * 119 / 100;
prixTtc + 100
```

Résultat :
```
DATA SEGMENT
	prixHt DD
	prixTtc DD
DATA ENDS
CODE SEGMENT
	mov eax, 200
	mov prixHt, eax
	mov eax, prixHt
	push eax
	mov eax, 119
	pop ebx
	mul eax, ebx
	push eax
	mov eax, 100
	pop ebx
	div ebx, eax
	mov eax, ebx
	mov prixTtc, eax
CODE ENDS
```


## Exemple 2

Programme :
```
let a = input;
let b = input;
while (0 < b)
do (let aux=(a mod b); let a=b; let b=aux );
output a .
```

Résultat :
```
DATA SEGMENT
	b DD
	a DD
	aux DD
DATA ENDS
CODE SEGMENT
	in eax
	mov a, eax
	in eax
	mov b, eax
debut_while_1:
	mov eax, 0
	push eax
	mov eax, b
	pop ebx
	sub eax,ebx
	jle faux_gt_1
	mov eax,1
	jmp sortie_gt_1
faux_gt_1:
	mov eax,0
sortie_gt_1:
	jz sortie_while_1
	mov eax, b
	push eax
	mov eax, a
	pop ebx
	mov ecx,eax
	div ecx,ebx
	mul ecx,ebx
	sub eax,ecx
	mov aux, eax
	mov eax, b
	mov a, eax
	mov eax, aux
	mov b, eax
	jmp debut_while_1
sortie_while_1:
	mov eax, a
	out eax
CODE ENDS
```


## Exemple 3 : un compteur

Programme : compte de b à a avec un pas de c.
```
let a = input; // limite du compteur
let b = 0;
let c = input; // pas du compteur
output b;
while (b < a)
do (let b = b + c; output b;);
.
```

Résultat :
```
DATA SEGMENT
	a DD
	b DD
	c DD
DATA ENDS
CODE SEGMENT
	in eax
	mov a, eax
	mov eax, 0
	mov b, eax
	in eax
	mov c, eax
	mov eax, b
	out eax
debut_while_1:
	mov eax, b
	push eax
	mov eax, a
	pop ebx
	sub eax, ebx
	jle faux_gt_1
	mov eax, 1
	jmp sortie_gt_1
faux_gt_1:
	mov eax, 0
sortie_gt_1:
	jz sortie_while_1
	mov eax, b
	push eax
	mov eax, c
	pop ebx
	add eax, ebx
	mov b, eax
	mov eax, b
	out eax
	jmp debut_while_1
sortie_while_1:
CODE ENDS
```


## Exemple 4 : calcul d'un pourcentage

Programme : calcule le pourcentage de a par rapport à b.

```
let v = input; // valeur
let t = input; /* sur total */
let p = (100 * v) / t;
output p;
.
```

Résultat :
```
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
```

