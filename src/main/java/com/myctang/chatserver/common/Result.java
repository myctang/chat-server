package com.myctang.chatserver.common;

public record Result<ResultT, ErrorT>(
        ResultT result,
        ErrorT error
) {
    public static <ResultT, Error> Result<ResultT, Error> error(Error error) {
        return new Result<>(null, error);
    }

    public static <ResultT, ErrorT> Result<ResultT, ErrorT> result(ResultT result) {
        return new Result<>(result, null);
    }

    public boolean isFailed() {
        return error != null;
    }

    public boolean isSuccessful() {
        return result != null;
    }
}
