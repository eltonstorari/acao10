<?php 

include "../conexao.php";
if (isset($_GET['id'])) {

	// code...
	$id = $_GET['id'];
	$sql = "DELETE FROM usuarios WHERE id = ?";


	$stm = $conexao->prepare($sql);

	$stm->bind_param('i', $id);
	if ($stm->execute()) {
		// code...
		echo "removido";
	} else {
		echo "erro";
	}
	mysqli_close($conexao);
} else {

	echo "naoExiste";
}






?>