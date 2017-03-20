# README


## ClearTkGenderAnnotator

Detects gender={m,f,o} of figures automatically.


### Evaluation results 

```
P	R	F1	#gold	#system	#correct
0.836	0.836	0.836	219	219	183	OVERALL
0.938	0.900	0.918	50	48	45	f
0.828	0.957	0.888	141	163	135	m
0.375	0.107	0.167	28	8	3	o

Predicted Class →
↓ Actual Class
          f     m     o Total
    f    45     5     0    50
    m     1   135     5   141
    o     2    23     3    28
Total    48   163     8 
```