<?php 

include "../conexao.php"; 

$id = $_POST["id"];
$nome = $_POST["nome"];
$email = $_POST["email"];
$senha = $_POST["senha"];
$nivel = $_POST["nivel"];
$imagem = $_POST["imagem"];
$url_imagem = "imagens/".$nome.".jpg";
$path = "imagens/".$nome.".jpg";

file_put_contents($path, base64_decode($imagem) );
$bytesAquivo = file_get_contents($path);

$sql = "INSERT INTO usuarios VALUES (?,?,?,?,?,?,?)";
$stm = $conexao->prepare($sql);
$stm->bind_param('sssssss', $id, $nome, $email, $senha, $nivel, $bytesAquivo, $url_imagem );


if($stm->execute()){
    echo "registra";
} else {
   echo "Não Registrado";

}

mysqli_close($conexao);




?>