/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services.Interfaces;

import java.util.List;


/**
 *
 * @author Le Parrain
 */
public interface IdelegationType<O,I,NV> {
    void add(O t);
    void delete(I id);
    O findById(I id);
    List<O> findBynv(NV nv);
    List<O> getAll();
}
