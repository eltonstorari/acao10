<?php 

include "../conexao.php"; 

$nome = $_POST["nome"];
$descricao = $_POST["descricao"];
$telefone = $_POST["telefone"];
$whatsapp = $_POST["whatsapp"];
$cep = $_POST["cep"];
$cidade = $_POST["cidade"];
$bairro = $_POST["bairro"];
$rua = $_POST["rua"];
$numero = $_POST["numero"];
$email = $_POST["email"];
$imagem = $_POST["imagem"];
$url_imagem = "imagens/".$nome.".jpg";
$path = "imagens/".$nome.".jpg";

file_put_contents($path, base64_decode($imagem) );


$sql = "INSERT INTO clubes (nome, descricao, telefone, whatsapp, cep, cidade, bairro, rua, numero, email, url_logo)
VALUES (?,?,?,?,?,?,?,?,?,?,?)";
$stm = $conexao->prepare($sql);
$stm->bind_param('sssssssssss', $nome,$descricao,$telefone, $whatsapp, $cep, $cidade, $bairro, $rua, $numero, $email, $url_imagem);


if($stm->execute()){
    echo "registra";
} else {
   echo "Não Registrado";

}

mysqli_close($conexao);




?>