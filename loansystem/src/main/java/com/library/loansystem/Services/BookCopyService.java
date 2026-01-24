package com.library.loansystem.Services;


import com.library.loansystem.Entities.BookCopy;

import java.util.List;
import java.util.Optional;

public interface BookCopyService {
    public BookCopy selectAvailableCopy (Long bookId);

}
