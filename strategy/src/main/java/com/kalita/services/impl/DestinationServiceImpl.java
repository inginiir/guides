package com.kalita.services.impl;

import com.kalita.model.Destination;
import com.kalita.repo.DestinationRepo;
import com.kalita.services.DestinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class DestinationServiceImpl implements DestinationService {

    private final DestinationRepo destinationRepo;

    @Override
    public List<Destination> getDestinations() {
        return StreamSupport.stream(destinationRepo.findAll().spliterator(), false).toList();
    }
}
