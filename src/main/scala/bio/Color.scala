package bio

import scala.collection.immutable.Map

    
object Color {
	val de2c = Map('A'->'0',
				   'C'->'1',
				   'G'->'2',
				   'T'->'3',
				   'N'->'4',
				   'a'->'0',
				   'c'->'1',
				   'g'->'2',
				   't'->'3',
				   'n'->'4',
				   '.'->'.')

	val base2num = Map('A'->0,
				   	   'C'->1,
				   	   'G'->2,
				   	   'T'->3,
				   	   'N'->4,
				   	   'a'->0,
				   	   'c'->1,
				   	   'g'->2,
				   	   't'->3,
				   	   'n'->4,
				   	   '.'->4)
				   
	val c2de =de2c.map(x => (x._2,x._1))

/*
    color2seq['A']['0'] = 'A';
    color2seq['A']['1'] = 'C';
    color2seq['A']['2'] = 'G';
    color2seq['A']['3'] = 'T';
    
    color2seq['C']['0'] = 'C';
    color2seq['C']['1'] = 'A';
    color2seq['C']['2'] = 'T';
    color2seq['C']['3'] = 'G';
    
    color2seq['G']['0'] = 'G';
    color2seq['G']['1'] = 'T';
    color2seq['G']['2'] = 'A';
    color2seq['G']['3'] = 'C';
    
    color2seq['T']['0'] = 'T';    
    color2seq['T']['1'] = 'G';
    color2seq['T']['2'] = 'C';
    color2seq['T']['3'] = 'A';
*/
	
	val c2b = Map('A' -> Map('0'->'A',
							 '1'->'C',
							 '2'->'G',
							 '3'->'T',
							 '4'->'N',
							 '.'->'N'),
				  'C' -> Map('0'->'C',
							 '1'->'A',
							 '2'->'T',
							 '3'->'G',
							 '4'->'N',
							 '.'->'N'),
				  'G' -> Map('0'->'G',
							 '1'->'T',
							 '2'->'A',
							 '3'->'C',
							 '4'->'N',
							 '.'->'N'),
				  'T' -> Map('0'->'T',
							 '1'->'G',
							 '2'->'C',
							 '3'->'A',
							 '4'->'N',
							 '.'->'N'),
				  'N' -> Map('0'->'N',
							 '1'->'N',
							 '2'->'N',
							 '3'->'N',
							 '4'->'N',
							 '.'->'N'))

	def decodeFirst(color:String) = {
		c2b(color(0))(color(1)) + color.drop(2) 
	}
	
	def de2color(de:String):String = {
		de.map(c => de2c(c))
	}
}