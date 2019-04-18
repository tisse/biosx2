package tisse.service;

import tisse.dto.Dto;

import java.util.List;

public abstract class BaseReportWriter<D extends Dto> {

    public abstract byte[] process(List<D> dtos);

}
