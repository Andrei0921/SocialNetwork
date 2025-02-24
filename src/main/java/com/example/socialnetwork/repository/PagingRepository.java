package com.example.socialnetwork.repository;


import com.example.socialnetwork.domain.Entity;
import com.example.socialnetwork.utils.paging.Page;
import com.example.socialnetwork.utils.paging.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>>extends Repository<ID, E> {
    public Page<E> findAllOnPage(Pageable pageable);
}