package com.sashimi.instructorapplication.application.port;

import java.util.List;

public interface DocxPort {

    List<String> extractMainCareers(byte[] fileBytes);
}
