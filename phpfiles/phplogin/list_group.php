<?php

$member = $_POST['user'];
$date = date("Y-m-d");

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
if ($member){ 
  $queryreg = mysql_query("              
			  SELECT groups_id_fk FROM jnct_users_groups WHERE users_id_fk='$dbuserid'             
             ");
            if($queryreg) {
			   while($row = mysql_fetch_assoc($queryreg)){ 
                 $dbgroupid= $row['groups_id_fk'];  				 
				 $group_query = mysql_query("              
			                 SELECT group_name FROM groups WHERE group_id='$dbgroupid'             
                            ");
				  if($group_query){
				    while($row = mysql_fetch_assoc($group_query)){ 
				       $b['group'] = $row['group_name'];
					   $b['id'] = $dbgroupid;
					   array_push($a,$b);
					 }
				  } else {
				     die("error, error");
				  }		 				 
               }			   
			   echo json_encode($a);			   
			} else {
			  die("Fail, user no member of group!");
			}
} else {
   die("Fail no valid user entered!");
}

?>