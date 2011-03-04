package bio

import scala.collection.immutable.Map

/*
 * '\0','\1','\2','\3','\4','\5','\6','\7','\10','\11','\12','\13','\14','\15','\16','\17','\20','\21','\22','\23','\24','\25','\26','\27','\30','\31','\32','\33','\34','\35','\36','\37',' ','!','"','#','$','%','&',''','(',')','*','+',',','-','.','/','0','1','2','3','4','5','6','7','8','9',':',';','<','=','>','?','@','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','[','\',']','^','_','`','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','{','|','}','~','\177','\200','\201','\202','\203','\204','\205','\206','\207','\210','\211','\212','\213','\214','\215','\216','\217','\220','\221','\222','\223','\224','\225','\226','\227','\230','\231','\232','\233','\234','\235','\236','\237','\240','\241','\242','\243','\244','\245','\246','\247','\250','\251','\252','\253','\254','\255','\256','\257','\260','\261','\262','\263','\264','\265','\266','\267','\270','\271','\272','\273','\274','\275','\276','\277','\300','\301','\302','\303','\304','\305','\306','\307','\310','\311','\312','\313','\314','\315','\316','\317','\320','\321','\322','\323','\324','\325','\326','\327','\330','\331','\332','\333','\334','\335','\336','\337','\340','\341','\342','\343','\344','\345','\346','\347','\350','\351','\352','\353','\354','\355','\356','\357','\360','\361','\362','\363','\364','\365','\366','\367','\370','\371','\372','\373','\374','\375','\376'
 */
    
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
				  
	val num2base = Array('A','C','G','T','N')
	
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

	val color2num = Map('0'->0,
				   	    '1'->1,
				   	    '2'->2,
				   	    '3'->3,
				   	    '.'->4)
				   	    
	val num2color = Array('0','1','2','3','.')
	
	val c2de =de2c.map(x => (x._2,x._1))

	
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
	
	def revcompV=Array('\0','\1','\2','\3','\4','\5','\6','\7','\10','\11','\12','\13','\14','\15',
			'\16','\17','\20','\21','\22','\23','\24','\25','\26','\27','\30','\31','\32','\33','\34',
			'\35','\36','\37',
			' ','!','"','#','$','%','&','\'','(',')','*','+',',','-','.','/',
			'0','1','2','3','4','5','6','7','8','9',':',';','<','=','>','?',
			'@','T','B','G','D','E','F','C','H','I','J','K','L','M','N','O',
			'P','Q','R','S','A','U','V','W','X','Y','Z','[','\\',']','^','_',
			'`','a','b','g','d','e','f','c','h','i','j','k','l','m','n','o','p',
			'q','r','s','t','u','v','w','x','y','z','{','|','}','~',
			'\177','\200','\201','\202','\203','\204','\205','\206','\207','\210',
			'\211','\212','\213','\214','\215','\216','\217','\220','\221','\222',
			'\223','\224','\225','\226','\227','\230','\231','\232','\233','\234',
			'\235','\236','\237','\240','\241','\242','\243','\244','\245','\246',
			'\247','\250','\251','\252','\253','\254','\255','\256','\257','\260',
			'\261','\262','\263','\264','\265','\266','\267','\270','\271','\272',
			'\273','\274','\275','\276','\277','\300','\301','\302','\303','\304',
			'\305','\306','\307','\310','\311','\312','\313','\314','\315','\316',
			'\317','\320','\321','\322','\323','\324','\325','\326','\327','\330',
			'\331','\332','\333','\334','\335','\336','\337','\340','\341','\342',
			'\343','\344','\345','\346','\347','\350','\351','\352','\353','\354',
			'\355','\356','\357','\360','\361','\362','\363','\364','\365','\366',
			'\367','\370','\371','\372','\373','\374','\375','\376')
				
			
    def revcomp(x:Char) = revcompV(x)
    
	def de2color(de:String):String = {
		de.map(c => de2c(c))
	}
}