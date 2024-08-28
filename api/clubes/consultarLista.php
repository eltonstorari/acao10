<?php 

include "../conexao.php"; 

$json = array();


$consulta = "SELECT id, nome, descricao, telefone, whatsapp, cep, cidade, bairro, rua, numero, email, url_logo FROM clubes";
$resultado = mysqli_query($conexao, $consulta);

while($registro = mysqli_fetch_array($resultado)){
	$json['clube'][] = $registro;
	//echo $registro['id']. ' - ' .$registro['nome']. ' - ' .$registro['email']. ' - ' .$registro['senha']. ' - ' .$registro['nivel']. '<br/>';


	
}

	mysqli_close($conexao);
	echo json_encode($json);



?>