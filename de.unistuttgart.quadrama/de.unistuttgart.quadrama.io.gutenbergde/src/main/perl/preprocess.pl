#!/usr/bin/perl

use warnings;

my $in = 0;
my $d = 0;

while(<>) {
	if (/id="gutenb">/) {
		$in = 1;
	}
	
	if ($in) {
		if (/\<div/) {
			$d++;
		} 
		if (/\<\/div/) {
			$d--;
		}
		print $_;
	}
	
	if ($d ==0 ) {
		$in = 0;
	}
}