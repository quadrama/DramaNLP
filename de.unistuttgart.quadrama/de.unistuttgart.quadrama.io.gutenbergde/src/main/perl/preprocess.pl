#!/usr/bin/perl

use warnings;
use HTML::Entities;
use utf8;


binmode STDOUT, ':utf8';
my $in = 0;
my $d = 0;

#print "<div>\n";

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
		s/id="gutenb"/class="gutenb"/;
		print decode_entities($_);
	}
	
	if ($d ==0 ) {
		$in = 0;
	}
	
	

}
#print "</div>\n";