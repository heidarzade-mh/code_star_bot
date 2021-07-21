import java.io.Serializable;

public enum ChatMode implements Serializable {
    Start,
    FamilyName,
    PhoneNumber,
    Address,
    CodePost,
    FinishGetInfo,
    Public,
    Private,

    EditName,
    EditFamilyName,
    EditAddress,
    EditPhoneNumber,
    EditPostCode
}
