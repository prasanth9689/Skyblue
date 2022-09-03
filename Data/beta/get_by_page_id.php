<?php


 
   // include('../../web/server_config/connect.php');
   include 'connect.php';
 
 
  $user_id = $_POST['user_id'];
  $page_id = $_POST['page_id'];
// $query = '%bmw%';
 
$Sql_Query = "SELECT post_table.id , 
       post_table.page_id as page_id , 
       post_table.user_id as post_user_id , 
       post_table.common_id as common_id, 
       post_table.text as post_text,
       post_table.url as url, 
       post_table.post_type as post_type ,
       post_table.date_added as date_added ,
       page_table.logo_url as profile_url,
       page_table.name as user_name , 
       COUNT(DISTINCT comments_table.id) as comments ,
       COUNT(DISTINCT likes_table.id) as likes ,
       user_table.cover_picture_url as user_cover ,
       likes_table_status.status as status 

    FROM page_post post_table
    
     LEFT JOIN   
   pages page_table
     ON post_table.page_id = page_table.id 
    
    LEFT JOIN   
   comments comments_table
     ON post_table.id = comments_table.post_id 
     
     LEFT JOIN
      upload_post_image_likes likes_table
     ON post_table.id = likes_table.post_id  
        
    LEFT JOIN 
      users user_table ON post_table.user_id = user_table.id
    
     LEFT JOIN 
      upload_post_image_likes likes_table_status ON  likes_table_status.user_id = '$user_id' AND likes_table_status.status = '1' AND likes_table_status.post_id =  post_table.id
      
      WHERE post_table.page_id = '$page_id' GROUP BY post_table.id  ORDER BY `id` DESC";
 
 
$result=mysqli_query($con,$Sql_Query);
 
$data=array();

while($row=mysqli_fetch_assoc($result)){
//	$data[]=$row;                //  old proportice work well with []

 //array_push($data, array("id"=>$row['id'],"video_name"=>$row['video_name']));


array_push($data, array("id"=>$row["id"] , "user_name"=>$row["user_name"] , "url"=>$row["url"] , "post_text"=>$row["post_text"], "placeholder_url"=>$row["placeholder_url"] , "likes"=>$row["likes"], "comments"=>$row["comments"], "profile_url"=>$row["profile_url"] , "status"=>$row["status"] , "id"=>$row["id"]  , "date_added"=>$row["date_added"], "user_cover"=>$row["user_cover"] , "post_user_id"=>$row["post_user_id"]));

 
}
 
 
 
    header('Content-Type:Application/json');
            
   // echo json_encode($data);
   // print(json_encode(array_reverse($data)));
//echo(json_encode(array_reverse($data, JSON_FORCE_OBJECT)));



//print(json_encode(array("status"=>"true", "message" => "Data fetched successfully!","data" =>$data)));
print(json_encode(array("data" =>$data)));


 
?>

 