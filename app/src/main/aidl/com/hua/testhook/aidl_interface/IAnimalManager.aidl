// IAnimalManager.aidl
package com.hua.testhook.aidl_interface;

import com.hua.testhook.aidl_model.Animal;

// Declare any non-default types here with import statements

interface IAnimalManager {

     void addAnimal(in Animal animal);

     List<Animal> getAnimals();
}