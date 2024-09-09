package com.spedire.Spedire.models;

import com.spedire.Spedire.enums.OrderType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Document
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class Order {

    @Id
    private String id;

    private String itemDescription;

    private String itemName;

    private BigDecimal itemValue;

    private Date dueDate;

    private LocalTime dueTime;

    private String picture;

    private String senderId;

    private String carriedId;

    private String senderName;

    private String senderLocation;

    private String senderTown;

    private String receiverLocation;
    private OrderType orderType;

    private String carrierTown;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getItemValue() {
        return itemValue;
    }

    public void setItemValue(BigDecimal itemValue) {
        this.itemValue = itemValue;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public LocalTime getDueTime() {
        return dueTime;
    }

    public void setDueTime(LocalTime dueTime) {
        this.dueTime = dueTime;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getCarriedId() {
        return carriedId;
    }

    public void setCarriedId(String carriedId) {
        this.carriedId = carriedId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderLocation() {
        return senderLocation;
    }

    public void setSenderLocation(String senderLocation) {
        this.senderLocation = senderLocation;
    }

    public String getSenderTown() {
        return senderTown;
    }

    public void setSenderTown(String senderTown) {
        this.senderTown = senderTown;
    }

    public String getReceiverLocation() {
        return receiverLocation;
    }

    public void setReceiverLocation(String receiverLocation) {
        this.receiverLocation = receiverLocation;
    }

    public String getCarrierTown() {
        return carrierTown;
    }

    public void setCarrierTown(String carrierTown) {
        this.carrierTown = carrierTown;
    }

    public String getReceiverTown() {
        return receiverTown;
    }

    public void setReceiverTown(String receiverTown) {
        this.receiverTown = receiverTown;
    }

    public String getSenderPhoneNumber() {
        return senderPhoneNumber;
    }

    public void setSenderPhoneNumber(String senderPhoneNumber) {
        this.senderPhoneNumber = senderPhoneNumber;
    }

    public String getReceiverPhoneNumber() {
        return receiverPhoneNumber;
    }

    public void setReceiverPhoneNumber(String receiverPhoneNumber) {
        this.receiverPhoneNumber = receiverPhoneNumber;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getCarrierImage() {
        return carrierImage;
    }

    public void setCarrierImage(String carrierImage) {
        this.carrierImage = carrierImage;
    }

    public String getCarrierFullName() {
        return carrierFullName;
    }

    public void setCarrierFullName(String carrierFullName) {
        this.carrierFullName = carrierFullName;
    }

    public String getCarrierLocation() {
        return carrierLocation;
    }

    public void setCarrierLocation(String carrierLocation) {
        this.carrierLocation = carrierLocation;
    }

    public String getCarrierPhoneNumber() {
        return carrierPhoneNumber;
    }

    public void setCarrierPhoneNumber(String carrierPhoneNumber) {
        this.carrierPhoneNumber = carrierPhoneNumber;
    }

    public boolean isPayState() {
        return payState;
    }

    public void setPayState(boolean payState) {
        this.payState = payState;
    }

    public String getPickUpNote() {
        return pickUpNote;
    }

    public void setPickUpNote(String pickUpNote) {
        this.pickUpNote = pickUpNote;
    }

    public String getDropOffNote() {
        return dropOffNote;
    }

    public void setDropOffNote(String dropOffNote) {
        this.dropOffNote = dropOffNote;
    }

    private String receiverTown;

    private String senderPhoneNumber;

    private String receiverPhoneNumber;

    private String receiverName;

    private String carrierImage;

    private String carrierFullName;

    private String carrierLocation;

    private String carrierPhoneNumber;

    private boolean payState;

    private String pickUpNote;

    private String dropOffNote;

    private OrderPayment orderPayment;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}


