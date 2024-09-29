<?php 

include "../conexao.php";

$json = array();

$consulta = "SELECT cnpj, nome, cadastro, propagandas, propagandas_ativas, whatsapp, instagram, facebook, email, contato, site, url_logo FROM parceiros";
$resultado = mysqli_query($conexao, $consulta);

while($registro = mysqli_fetch_array($resultado)){
    $json['parceiro'][] = $registro;
}
    mysqli_close($conexao);
    echo json_encode($json);
?>  
