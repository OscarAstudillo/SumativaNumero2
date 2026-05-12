package TechStoreChile.techstore_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import TechStoreChile.techstore_api.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

}
