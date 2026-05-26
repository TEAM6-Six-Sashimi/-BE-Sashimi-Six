package com.sashimi.category.presentation.api.response;

import java.util.List;

public record CategoryGroupResponse(String name, List<String> subCategories) {}