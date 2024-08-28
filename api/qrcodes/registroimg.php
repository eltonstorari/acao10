<?php 

include "../conexao.php"; 

$codigo = $_POST["codigo"];
$clube = $_POST["clube"];
$caixa = $_POST["caixa"];
$descricao = $_POST["descricao"];

$sql = "INSERT INTO qrcodes VALUES (?,?,?,?)";
$stm = $conexao->prepare($sql);
$stm->bind_param('ssss', $codigo, $clube, $caixa, $descricao);


if($stm->execute()){
    echo "registra";
} else {
   echo "Não Registrado";

}

mysqli_close($conexao);




?>