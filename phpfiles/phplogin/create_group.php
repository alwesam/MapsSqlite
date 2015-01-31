<?php

$creator = $_POST['name'];
$group = $_POST['group'];
$description = $_POST['description'];
$type = $_POST['type'];
$date = date("Y-m-d");

//connect to db
//$connect = mysql_connect("localhost","root","Nazkin81");
//mysql_select_db("phplogin");

include_once './db_functions_group.php';
$db = new DB_Functions();

//select user id
$user_query = mysql_query("SELECT * FROM users WHERE username='$creator'");
 if($user_query){    
	 while($row = mysql_fetch_assoc($user_query)){ 
        $dbuserid = $row['id'];  
     }
 } else {
     die($creator);  
 }

$namecheck = mysql_query("SELECT group_name FROM groups WHERE group_name='$group'");
$count = mysql_num_rows($namecheck);

 if($count !=0){
    die("Group with this name already exists");
  }

if($creator&&$group&&$description&&$type)
{
        if(strlen($group)>50||strlen($group)<4){
             echo "Group name must be between 4 and 50 characters";             
         } else {		 
             $queryreg = mysql_query("              
			  INSERT INTO groups VALUES('','$group','$description','$type','$creator','$date');             
             ");		
			 
			  //select group id
			  if($queryreg) {
                  $group_query = mysql_query("SELECT group_id FROM groups WHERE group_name='$group'");
                  if($group_query){    
	                while($row = mysql_fetch_assoc($group_query)){ 
                       $dbgroupid = $row['group_id'];  
                    }
                  } else {
                     die("Group with this name doesn't exist");
                 }
			  }  else {			    
				die("Failed to insert group");			   
			  }			 
			 
			  $queryreg2 = mysql_query("              
			        INSERT INTO jnct_users_groups VALUES('','$dbuserid','$dbgroupid','$date');             
               ");
			 
			  //join group	
              if($queryreg2) {		 
                $a["status"] = True;
	            echo json_encode($a);
              }			 
        } 
}
else
  die("Please fill in all fields");

?>
