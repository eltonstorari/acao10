<?php 

include "../conexao.php";

$id = $_POST["id"];
$nome = $_POST["nome"];
$email = $_POST["email"];
$senha = $_POST["senha"];
$nivel = $_POST["nivel"];
$imagem = $_POST["imagem"];

$path = "imagens/$nome.jpg";
$url = "imagens/".$nome.".jpg";




file_put_contents($path, base64_decode($imagem));

$bytesArquivo = file_get_contents($path);

$sql = "UPDATE usuarios SET nome = ?, email = ?, senha = ?, nivel = ?, imagem = ?, url_imagem = ? WHERE id = ?";
$stm =  $conexao->prepare($sql);
$stm->bind_param('ssssssi', $nome, $email, $senha, $nivel, $bytesArquivo, $url, $id);

if($stm->execute()){
	echo "registra";

} else{
	echo "Não Registrado";
}

mysqli_close($conexao);

?>