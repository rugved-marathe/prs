package io.pms.api.exception;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Builder
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class Errors {
    private @NonNull String source;
    private @NonNull String message;
    private @NonNull String detailedMessage;
}

