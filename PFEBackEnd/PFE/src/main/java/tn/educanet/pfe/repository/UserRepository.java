package tn.educanet.pfe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import tn.educanet.pfe.persistence.User;

@Repository
public interface UserRepository  extends CrudRepository<User, Long>,
JpaRepository<User, Long>, JpaSpecificationExecutor<User>,
PagingAndSortingRepository<User, Long>{

}
