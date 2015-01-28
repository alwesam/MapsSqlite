<?php

$member = $_POST['user'];

include_once './db_functions_group.php';
$db = new DB_Functions();

//select user id
$user_query = mysql_query("SELECT id FROM users WHERE username='$member'");
if($user_query){    
   while($row = mysql_fetch_assoc($user_query)){ 
        $dbuserid = $row['id'];  
    }
} else {
     die("User with this name doesn't exist");  
}

$a = array();
$b = array();	
                   				 
$group_query = mysql_query("SELECT * FROM groups");

if($group_query){

	while($row = mysql_fetch_assoc($group_query)){ 
	   $b['group'] = $row['group_name'];
	   $b['description'] = $row['group_description'];
	   $b['type'] = $row['group_type'];
	   
	   $dbgroupid = $row['group_id'];	   
	   $queryreg = mysql_query("              
		  SELECT users_groups_id FROM jnct_users_groups WHERE users_id_fk='$dbuserid' 
                       AND groups_id_fk='$dbgroupid'			  
             ");
			 
	   //TODO fix
	    if($queryreg){
		   $b['join_status'] = "no";
           while($row = mysql_fetch_assoc($queryreg)){ 
	         $b['join_status'] = "yes";
           }
        }
	   
	   array_push($a,$b);
	}	
	
} else {
	die("No Groups Found");
}		 				 
               			   
echo json_encode($a);			   

?>
