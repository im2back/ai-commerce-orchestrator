package io.github.froideexplica.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.froideexplica.domain.entities.Product;
import io.github.froideexplica.dto.inputdata.NewProductDTO;
import io.github.froideexplica.dto.inputdata.ProductMassiveReplenishmentDTO;
import io.github.froideexplica.dto.inputdata.PurchasedItemDTO;
import io.github.froideexplica.dto.inputdata.UndoPurchaseDTO;
import io.github.froideexplica.dto.outputdata.MassiveReplenishmentResponseDTO;
import io.github.froideexplica.dto.outputdata.ProductDTO;
import io.github.froideexplica.dto.outputdata.UpdatedStockResponseDTO;
//import io.github.froideexplica.infra.logging.LogContext;
import io.github.froideexplica.repositories.ProductRepository;
import io.github.froideexplica.service.exceptions.ProductNotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ProductService {

//	private static final Logger log = LoggerFactory.getLogger(ProductService.class);

	@Inject
	ProductRepository repository;

	@Transactional
	public ProductDTO findProductById(Long id) {
		Product product = repository.findById(id);

		if (product == null) {
			throw new ProductNotFoundException("Product Not found for id: " + id);
		}

		return new ProductDTO(product);
	}

	@Transactional
	public ProductDTO findProductByCode(String code) {
		Product product = repository.findByCode(code)
				.orElseThrow(() -> new ProductNotFoundException("Product Not found for code: " + code));

		return new ProductDTO(product);
	}

	@Transactional
	public ProductDTO saveNewProduct(NewProductDTO p) {
		Product product = new Product(p);

		repository.persist(product);

//		LogContext.put("Product Created", product.getId().toString(), "user id");
//		log.info("✅ Produto cadastrado com sucesso");
//		LogContext.clear();

		return new ProductDTO(product);
	}

	@Transactional
	public void deleProductById(Long id) {
		Product product = repository.findById(id);

		if (product == null) {
			throw new ProductNotFoundException("Product Not found for id: " + id);
		}

		repository.delete(product);
	}

	@Transactional
	public List<UpdatedStockResponseDTO> updateQuantityProductsAfterPurchase(List<PurchasedItemDTO> dto) {
		List<Product> products = findProductsByCodes(dto);
		List<Product> listOfProductsThatHaveBeenUpdated =
				persistChangesInTheQuantityOfProducts(dto, products);

		return buildUpdateResponse(dto, listOfProductsThatHaveBeenUpdated);
	}

	private List<Product> persistChangesInTheQuantityOfProducts(
			List<PurchasedItemDTO> productsList,
			List<Product> products
	) {
		HashMap<String, Product> productHashMap =
				new HashMap<>(products.stream()
						.collect(Collectors.toMap(Product::getCode, p -> p)));

		for (PurchasedItemDTO p : productsList) {
			Product product = productHashMap.get(p.code());

			if (product == null) {
				throw new ProductNotFoundException("Product Not found for code: " + p.code());
			}

			product.setQuantity(product.getQuantity() - p.quantity());
		}

		return new ArrayList<>(productHashMap.values());
	}

	private List<UpdatedStockResponseDTO> buildUpdateResponse(
			List<PurchasedItemDTO> productsList,
			List<Product> products
	) {
		List<UpdatedStockResponseDTO> response = new ArrayList<>();

		HashMap<String, Product> productHashMap =
				new HashMap<>(products.stream()
						.collect(Collectors.toMap(Product::getCode, p -> p)));

		productsList.forEach(p -> {
			Product product = productHashMap.get(p.code());

			if (product == null) {
				throw new ProductNotFoundException("Product Not found for code: " + p.code());
			}

			response.add(new UpdatedStockResponseDTO(product, p.quantity()));
		});

		return response;
	}

	private List<Product> findProductsByCodes(List<PurchasedItemDTO> productsList) {
		List<String> productCodesList = new ArrayList<>();
		productsList.forEach(t -> productCodesList.add(t.code()));

		return repository.findByCodes(productCodesList);
	}

	@Transactional
	public void undoIndividualPurchase(UndoPurchaseDTO dto, String code) {
		Product product = repository.findByCode(code)
				.orElseThrow(() -> new ProductNotFoundException("Product Not found for code: " + code));

		product.setQuantity(product.getQuantity() + dto.quantity());
	}

	@Transactional
	public void updateProduct(ProductDTO dto, Long id) {
		Product product = repository.findById(id);

		if (product == null) {
			throw new ProductNotFoundException("Product Not found for id: " + id);
		}

		product.updateAttributes(dto);
	}

	@Transactional
	public List<MassiveReplenishmentResponseDTO> massiveReplenishment(
			List<ProductMassiveReplenishmentDTO> dtoIn
	) {
		List<MassiveReplenishmentResponseDTO> response = new ArrayList<>();

		List<String> extractedCodes = dtoIn.stream()
				.map(ProductMassiveReplenishmentDTO::code)
				.collect(Collectors.toList());

		List<Product> products = repository.findByCodes(extractedCodes);

		Map<String, Product> productMap = products.stream()
				.collect(Collectors.toMap(Product::getCode, p -> p));

		for (ProductMassiveReplenishmentDTO p : dtoIn) {
			Product product = productMap.get(p.code());

			if (product == null) {
				throw new ProductNotFoundException("Product Not found for code: " + p.code());
			}

			product.setQuantity(product.getQuantity() + p.quantity());
			response.add(new MassiveReplenishmentResponseDTO(product, p.quantity()));
		}

		return response;
	}

	@Transactional
	public List<ProductDTO> listAllProducts() {
		return repository.listAll()
				.stream()
				.map(ProductDTO::new)
				.toList();
	}
}


