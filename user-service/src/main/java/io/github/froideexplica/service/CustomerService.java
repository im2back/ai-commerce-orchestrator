package io.github.froideexplica.service;


import io.github.froideexplica.dto.input.PurchaseHistoryInDTO;
import io.github.froideexplica.dto.input.RegisterCustomerDTO;
import io.github.froideexplica.dto.output.*;
import io.github.froideexplica.model.Customer;
import io.github.froideexplica.model.PurchaseRecord;
import io.github.froideexplica.model.Status;
import io.github.froideexplica.repository.CustomerRepository;
import io.github.froideexplica.service.exceptions.CustomerNotFoundException;
import io.github.froideexplica.service.exceptions.PurchaseNotFoundException;
import io.github.froideexplica.validations.customervalidations.CustomerValidations;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.enterprise.inject.Instance;
@ApplicationScoped
public class CustomerService {

    @Inject
    private  CustomerRepository repository;

    @Inject
    Instance<CustomerValidations> customerValidations;

    @Inject
    PdfGenerator pdfGenerator;

    public CustomerDTO findCustomerByDocument(String document) {
        Customer customer = repository.findByDocument(document)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found for document: " + document
                ));
        return new CustomerDTO(customer);
    }

    private Customer findCustomerByDocumentInternal(String document) {
        return repository.findByDocument(document)
                .orElseThrow(() -> new RuntimeException("User not found for document: " + document));
    }

    public CustomerDTO findCustomerById(Long id) {
        Customer customer = repository.findById(id);

        if (customer == null) {
            throw new CustomerNotFoundException("User not found for id: " + id);
        }

        return new CustomerDTO(customer);
    }

    public CustomerDTO findCustomerWithUnpaidPurchases(String document) {
        Customer customer = findCustomerByDocumentInternal(document);
        customer.getPurchaseRecord().removeIf(t -> t.getStatus() == Status.PAGO);
        return new CustomerDTO(customer);
    }

    @Transactional
    public CustomerDTO saveNewCustomer(RegisterCustomerDTO dtoIn) {

        customerValidations.forEach(t -> t.valid(dtoIn));

        Customer customer = new Customer(dtoIn);

        repository.persist(customer);

        return new CustomerDTO(customer);
    }

    @Transactional
    public void logicalCustomerDeletion(String document) {
        Customer customer = findCustomerByDocumentInternal(document);
        customer.setActive(false);
        repository.persist(customer);
    }

    @Transactional
    public PurchaseHistoryOutDTO registerPurchase(PurchaseHistoryInDTO dtoRequest) {

        Customer customer = findCustomerByDocumentInternal(dtoRequest.document());
        addPurchaseToCustomerHistory(dtoRequest, customer);
        repository.persist(customer);

        return assembleResponse(dtoRequest, customer);
    }

    @Transactional
    public void registerPurchaseAsync(PurchaseHistoryInDTO dtoRequest) {
        Customer customer = findCustomerByDocumentInternal(dtoRequest.document());
        addPurchaseToCustomerHistory(dtoRequest, customer);
        repository.persist(customer);
    }

    private void addPurchaseToCustomerHistory(PurchaseHistoryInDTO dtoRequest, Customer customer) {
        Instant instant = Instant.now();
        Status status = Status.DEFINIR_STATUS.setStatus(customer.getDocument());

        List<PurchaseRecord> purchaseRecords = dtoRequest.products().stream()
                .map(dto -> new PurchaseRecord(dto, instant, customer, status)).collect(Collectors.toList());
        customer.getPurchaseRecord().addAll(purchaseRecords);
    }

    private PurchaseHistoryOutDTO assembleResponse(PurchaseHistoryInDTO dtoRequest, Customer customer) {
        List<PurchasedProductDTO> purchasedProducts = dtoRequest.products().stream()
                .map(p -> new PurchasedProductDTO(p.name(), p.quantity(), p.price())).collect(Collectors.toList());

        BigDecimal total = purchasedProducts.stream().map(p -> p.value().multiply(new BigDecimal(p.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PurchaseHistoryOutDTO(customer.getName(), purchasedProducts, total);
    }

    @Transactional
    public void undoPurchase(Long id) {
        repository.undoPurchase(id);
    }

    public void generatePurchaseInvoice(String document) {
        Customer customer = findCustomerByDocumentInternal(document);
        List<ProductDataToPdf> productDataToPdfList = getProductDataToPdfList(customer.getPurchaseRecord());

        pdfGenerator.generatePdf(productDataToPdfList, customer, getPath(customer.getName()));
    }

    private List<ProductDataToPdf> getProductDataToPdfList(List<PurchaseRecord> list) {
        list.removeIf(t -> t.getStatus().equals(Status.PAGO));
        var ProductDataToPdfList = list.stream().map(t -> new ProductDataToPdf(t)).collect(Collectors.toList());
        return ProductDataToPdfList;
    }

    private String getPath(String customerName) {
        String userHome = System.getProperty("user.home");
        String desktopDirectory = Paths.get(userHome).toString();
        var path = Paths.get(desktopDirectory, customerName + "_nota_fiscal_" + ".pdf").toString();
        return path;
    }

    @Transactional
    public void clearDebt(String document) {
        repository.updateStatusByCustomerDocumentNative("PAGO", document, "EM_ABERTO");
    }

    //@Transactional(readOnly = true)
    public DataForMetricsDTO metrics() {
        return new DataForMetricsDTO(
                repository.totalValueForLastMonth(),
                repository.partialValueOfTheCurrentMonth(),
                repository.partialVAlueForCurrentDay(),
                repository.totalOutstandingAmount(),
                getTotalValuesForLast7Days());
    }

    protected List<DailyTotalDTO> getTotalValuesForLast7Days() {
        List<Object[]> results = repository.findTotalValueForLast7DaysExcludingToday();
        List<DailyTotalDTO> dailyTotals = new ArrayList<>();

        for (Object[] result : results) {
            Date sqlDate = (java.sql.Date) result[0];
            BigDecimal totalValue = (BigDecimal) result[1];
            LocalDate purchaseDate = sqlDate.toLocalDate();
            dailyTotals.add(new DailyTotalDTO(purchaseDate, totalValue));
        }

        return dailyTotals;
    }

    @Transactional
    public void individualPayment(Long purchaseId) {
        Long exists = this.repository.existsPurchaseRecordById(purchaseId);
        if(exists == null || exists != 1L) {
            throw new PurchaseNotFoundException("Purchase not found for id "+ purchaseId);
        }

        this.repository.individualPayment(Status.PAGO.toString(), purchaseId);
    }

}